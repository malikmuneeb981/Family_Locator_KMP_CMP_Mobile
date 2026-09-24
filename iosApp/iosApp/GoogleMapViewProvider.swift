import UIKit
import GoogleMaps
import Shared
import CoreLocation
import ObjectiveC

// MARK: - Private associated-object key (typed pointer = unique key)

private var kMapHolderKey: UInt8 = 0

// MARK: - Marker Icon Factories

/// Blue pulsing dot — matches Android's ic_currentlocation drawable rendered via bitmapDescriptorFromPainter.
private func createCurrentLocationMarkerIcon() -> UIImage {
    let size = CGSize(width: 41, height: 41)
    let renderer = UIGraphicsImageRenderer(size: size)
    return renderer.image { context in
        let cgContext = context.cgContext
        let center = CGPoint(x: 20.5, y: 20.5)

        let primaryColor = UIColor(red: 0x32 / 255.0, green: 0x68 / 255.0, blue: 0xF0 / 255.0, alpha: 1.0)
        let haloColor   = primaryColor.withAlphaComponent(0.18)

        // Outer halo ring: radius 15.5, stroke width 10 — same as Android SVG asset
        cgContext.setLineWidth(10.0)
        cgContext.setStrokeColor(haloColor.cgColor)
        cgContext.strokeEllipse(in: CGRect(x: center.x - 15.5, y: center.y - 15.5, width: 31.0, height: 31.0))

        // Inner solid circle: radius 10.5
        cgContext.setFillColor(primaryColor.cgColor)
        cgContext.fillEllipse(in: CGRect(x: center.x - 10.5, y: center.y - 10.5, width: 21.0, height: 21.0))
    }
}

/// Orange ring marker for friends — mirrors Android's user_click_marker drawable used for friends.
private func createFriendMarkerIcon() -> UIImage {
    let size = CGSize(width: 34, height: 34)
    let renderer = UIGraphicsImageRenderer(size: size)
    return renderer.image { context in
        let cgContext = context.cgContext
        let center = CGPoint(x: 17, y: 17)

        // Outer circle
        let outerColor = UIColor(red: 0xFF / 255.0, green: 0x6B / 255.0, blue: 0x35 / 255.0, alpha: 1.0)
        cgContext.setFillColor(outerColor.cgColor)
        cgContext.fillEllipse(in: CGRect(x: center.x - 14, y: center.y - 14, width: 28, height: 28))

        // Inner white dot
        cgContext.setFillColor(UIColor.white.cgColor)
        cgContext.fillEllipse(in: CGRect(x: center.x - 7, y: center.y - 7, width: 14, height: 14))
    }
}

/// Blue semi-transparent dot for map-tap marker — mirrors Android's user_click_marker used for onMapClick.
private func createClickMarkerIcon() -> UIImage {
    let size = CGSize(width: 28, height: 28)
    let renderer = UIGraphicsImageRenderer(size: size)
    return renderer.image { context in
        let cgContext = context.cgContext
        let pinColor = UIColor(red: 0x32 / 255.0, green: 0x68 / 255.0, blue: 0xF0 / 255.0, alpha: 0.80)
        cgContext.setFillColor(pinColor.cgColor)
        cgContext.fillEllipse(in: CGRect(x: 0, y: 0, width: 28, height: 28))

        // White centre dot
        cgContext.setFillColor(UIColor.white.cgColor)
        cgContext.fillEllipse(in: CGRect(x: 9, y: 9, width: 10, height: 10))
    }
}

// MARK: - Location Delegate

/// Drives CLLocationManager updates → current-location marker + Kotlin callback.
/// Mirrors Android's `startLocationUpdates` + `LaunchedEffect(currentLocation)`.
private final class GMSLocationDelegate: NSObject, CLLocationManagerDelegate {

    private weak var mapView: GMSMapView?
    private let currentLocationMarker: GMSMarker
    /// Kotlin callback — receives KotlinDouble so the ViewModel can update state.
    private let onCurrentLocation: (KotlinDouble, KotlinDouble) -> Void

    /// Mirrors Android's `goToCurrentLocation` flag checked in `LaunchedEffect(currentLocation)`.
    var goToCurrentLocation: Bool
    /// Mirrors Android's `navigatedToCurrentLoc()` call inside `LaunchedEffect(currentLocation)`.
    var navigatedToCurrentLoc: () -> Void

    var lastKnownLocation: CLLocationCoordinate2D?

    init(
        mapView: GMSMapView,
        currentLocationMarker: GMSMarker,
        onCurrentLocation: @escaping (KotlinDouble, KotlinDouble) -> Void,
        goToCurrentLocation: Bool,
        navigatedToCurrentLoc: @escaping () -> Void
    ) {
        self.mapView               = mapView
        self.currentLocationMarker = currentLocationMarker
        self.onCurrentLocation     = onCurrentLocation
        self.goToCurrentLocation   = goToCurrentLocation
        self.navigatedToCurrentLoc = navigatedToCurrentLoc
    }

    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        let status = manager.authorizationStatus
        NSLog("[GMSLocationDelegate] authorizationStatus changed: %d", status.rawValue)
        if status == .authorizedWhenInUse || status == .authorizedAlways {
            manager.startUpdatingLocation()
            if let loc = manager.location {
                locationManager(manager, didUpdateLocations: [loc])
            }
        }
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last, let mapView = mapView else { return }
        let coord = location.coordinate
        lastKnownLocation = coord

        NSLog("[GMSLocationDelegate] didUpdateLocations: lat=%f, lng=%f", coord.latitude, coord.longitude)

        // Move current-location marker (mirrors Android's markerState.position = loc)
        currentLocationMarker.position = coord
        if currentLocationMarker.map == nil {
            currentLocationMarker.map = mapView
        }

        // Notify Kotlin — uses KotlinDouble to match the Kotlin Double type at the boundary
        onCurrentLocation(
            KotlinDouble(value: coord.latitude),
            KotlinDouble(value: coord.longitude)
        )

        // Animate camera + signal navigation — mirrors Android's:
        //   cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(loc, 16f))
        //   navigatedToCurrentLoc()
        if goToCurrentLocation {
            goToCurrentLocation = false
            let update = GMSCameraUpdate.setTarget(coord, zoom: 16.0)
            mapView.animate(with: update)
            navigatedToCurrentLoc()
        }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        NSLog("[GMSLocationDelegate] locationManager didFailWithError: %@", error.localizedDescription)
    }

    func moveToCurrentLocationIfAvailable() {
        guard let coord = lastKnownLocation, let mapView = mapView else { return }
        let update = GMSCameraUpdate.setTarget(coord, zoom: 16.0)
        mapView.animate(with: update)
        goToCurrentLocation = false
        navigatedToCurrentLoc()
    }
}

// MARK: - Map Click Delegate

/// Handles GMSMapView tap events → places click marker + fires Kotlin onMapClick.
/// Mirrors Android's `onMapClick = { userClickedMarkerState.position = it; onMapClick(…) }`.
private final class GMSMapClickDelegate: NSObject, GMSMapViewDelegate {

    private let onMapClick: (KotlinDouble, KotlinDouble) -> Void
    private let clickMarker: GMSMarker

    init(
        clickMarker: GMSMarker,
        onMapClick: @escaping (KotlinDouble, KotlinDouble) -> Void
    ) {
        self.clickMarker = clickMarker
        self.onMapClick  = onMapClick
    }

    func mapView(_ mapView: GMSMapView, didTapAt coordinate: CLLocationCoordinate2D) {
        // Place / move the tap marker (mirrors Android's userClickedMarkerState.position = it)
        clickMarker.position = coordinate
        clickMarker.map      = mapView

        // Fire Kotlin callback with KotlinDouble — matches (lat: Double, long: Double) -> Unit
        onMapClick(
            KotlinDouble(value: coordinate.latitude),
            KotlinDouble(value: coordinate.longitude)
        )
    }
}

// MARK: - Map State Holder

/// Owns all delegates, markers, and the CLLocationManager for a single GMSMapView instance.
/// Stored as an associated object on the UIView so it lives for exactly as long as the view —
/// this mirrors how Android retains MarkerState objects inside `remember { mutableMapOf<…>() }`.
final class GMSMapHolder: NSObject {

    private let mapView: GMSMapView
    private let locationManager: CLLocationManager
    private let locationDelegate: GMSLocationDelegate
    private let mapClickDelegate: GMSMapClickDelegate

    // Markers
    private let currentLocationMarker = GMSMarker()
    private let clickMarker           = GMSMarker()
    /// Keyed by senderName — mirrors Android's `friendMarkerStates: MutableMap<String, MarkerState>`.
    private var friendMarkers: [String: GMSMarker] = [:]

    // Cached icons (created once, shared across all marker instances)
    private static let currentLocationIcon = createCurrentLocationMarkerIcon()
    private static let friendMarkerIcon    = createFriendMarkerIcon()
    private static let clickMarkerIcon     = createClickMarkerIcon()

    init(
        mapView: GMSMapView,
        onMapClick: @escaping (KotlinDouble, KotlinDouble) -> Void,
        onCurrentLocation: @escaping (KotlinDouble, KotlinDouble) -> Void,
        goToCurrentLocation: Bool,
        navigatedToCurrentLoc: @escaping () -> Void
    ) {
        self.mapView = mapView

        locationDelegate = GMSLocationDelegate(
            mapView: mapView,
            currentLocationMarker: currentLocationMarker,
            onCurrentLocation: onCurrentLocation,
            goToCurrentLocation: goToCurrentLocation,
            navigatedToCurrentLoc: navigatedToCurrentLoc
        )

        mapClickDelegate = GMSMapClickDelegate(
            clickMarker: clickMarker,
            onMapClick: onMapClick
        )

        // Must be initialised in phase 1 (before super.init) because it is a `let` stored property.
        locationManager = CLLocationManager()

        super.init()

        // ── Phase 2: configure markers and start services ──────────────────────────

        // Current-location marker setup (mirrors Android's MarkerInfoWindow with ic_currentlocation)
        currentLocationMarker.icon         = GMSMapHolder.currentLocationIcon
        currentLocationMarker.groundAnchor = CGPoint(x: 0.5, y: 0.5)
        // Note: currentLocationMarker.map is attached once a valid coordinate is received

        // Click marker — hidden until first tap (map = nil mirrors Android's rememberMarkerState
        // before any click has occurred)
        clickMarker.icon        = GMSMapHolder.clickMarkerIcon
        clickMarker.groundAnchor = CGPoint(x: 0.5, y: 0.5)

        // Wire delegates
        mapView.delegate = mapClickDelegate

        locationManager.delegate        = locationDelegate
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.distanceFilter  = kCLDistanceFilterNone
        locationManager.requestWhenInUseAuthorization()
        locationManager.startUpdatingLocation()

        // If location is already available (e.g. permission already granted), handle it immediately!
        let status = locationManager.authorizationStatus
        if status == .authorizedWhenInUse || status == .authorizedAlways {
            locationManager.startUpdatingLocation()
            if let initialLoc = locationManager.location {
                locationDelegate.locationManager(locationManager, didUpdateLocations: [initialLoc])
            }
        }
    }

    // MARK: State update (called from UIKitView update block)

    /// Mirrors Android's combined `LaunchedEffect(myFriendsLatLongs)`,
    /// `LaunchedEffect(currentLocation)` (goToCurrentLocation flag), and
    /// `LaunchedEffect(goToFriendsLatLong)` (camera jump to a selected friend).
    func updateState(
        friends: [MyLatLng],
        goToCurrentLocation: Bool,
        goToFriendsLatLong: MyLatLng?,
        navigatedToCurrentLoc: @escaping () -> Void
    ) {
        // Propagate updated flag + callback into the location delegate
        locationDelegate.goToCurrentLocation  = goToCurrentLocation
        locationDelegate.navigatedToCurrentLoc = navigatedToCurrentLoc

        // If goToCurrentLocation is true and we already have a known location, animate to it now!
        if goToCurrentLocation {
            locationDelegate.moveToCurrentLocationIfAvailable()
        }

        // ── Friend markers ──────────────────────────────────────────────────────────────
        // Add or mutate markers — mirrors Android's:
        //   val state = friendMarkerStates.getOrPut(key) { MarkerState(LatLng(lat, lng)) }
        //   state.position = LatLng(lat, lng)
        var activeNames = Set<String>()

        for friend in friends {
            guard let name = friend.senderName,
                  let latObj = friend.latitude,
                  let lngObj = friend.longitude else { continue }

            // latitude/longitude are Kotlin Double? → KotlinDouble? at the boundary
            let lat = latObj.doubleValue
            let lng = lngObj.doubleValue
            activeNames.insert(name)

            let coordinate = CLLocationCoordinate2D(latitude: lat, longitude: lng)

            if let marker = friendMarkers[name] {
                // Mutate existing marker position (mirrors Android MarkerState.position = LatLng(…))
                marker.position = coordinate
                marker.snippet  = String(format: "Lat: %.5f, Lng: %.5f", lat, lng)
            } else {
                // Create new marker (mirrors Android's MarkerState being added to friendMarkerStates)
                let marker      = GMSMarker()
                marker.position = coordinate
                marker.icon     = GMSMapHolder.friendMarkerIcon
                marker.title    = name
                marker.snippet  = String(format: "Lat: %.5f, Lng: %.5f", lat, lng)
                marker.map      = mapView
                friendMarkers[name] = marker
            }
        }

        // Remove stale markers — mirrors Android's:
        //   friendMarkerStates.keys.retainAll(activeKeys)
        for key in Set(friendMarkers.keys).subtracting(activeNames) {
            friendMarkers[key]?.map = nil
            friendMarkers.removeValue(forKey: key)
        }

        // ── Camera jump to selected friend ─────────────────────────────────────────────
        // Mirrors Android's LaunchedEffect(goToFriendsLatLong):
        //   cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 16f))
        //   marker.showInfoWindow()
        if let target   = goToFriendsLatLong,
           let latObj   = target.latitude,
           let lngObj   = target.longitude,
           let name     = target.senderName {
            let lat  = latObj.doubleValue
            let lng  = lngObj.doubleValue
            let coord = CLLocationCoordinate2D(latitude: lat, longitude: lng)
            let update = GMSCameraUpdate.setTarget(coord, zoom: 16.0)
            mapView.animate(with: update)
            // Show the info window for that friend's marker (mirrors marker.showInfoWindow())
            if let marker = friendMarkers[name] {
                mapView.selectedMarker = marker
            }
        }
    }
}

// MARK: - Public API

/// Creates and configures a GMSMapView with full feature parity to Android's NativeGoogleMap.
///
/// Called once by `googleMapViewFactory` (set in iOSApp.swift) when the `UIKitView` first composes.
/// Mirrors the initial setup in Android's `NativeGoogleMap` composable:
///   - `rememberCameraPositionState` → initial camera at (33.6844, 73.0479) zoom 16
///   - `MapUiSettings` / `MapProperties`
///   - `startLocationUpdates` LaunchedEffect
func createGoogleMapView(
    onMapClick: @escaping (KotlinDouble, KotlinDouble) -> Void,
    currentLocation: @escaping (KotlinDouble, KotlinDouble) -> Void,
    goToCurrentLocation: Bool,
    navigatedToCurrentLoc: @escaping () -> Void
) -> UIView {
    // Initial camera position — mirrors Android's CameraPosition.fromLatLngZoom(LatLng(33.6844, 73.0479), 16f)
    // Using GMSMapViewOptions to avoid the deprecated GMSMapView(frame:camera:) initialiser.
    let camera  = GMSCameraPosition.camera(withLatitude: 33.6844, longitude: 73.0479, zoom: 16.0)
    let options = GMSMapViewOptions()
    options.camera = camera
    options.frame  = .zero
    let mapView = GMSMapView(options: options)

    // Match Android MapUiSettings + MapProperties
    mapView.settings.myLocationButton = false   // myLocationButtonEnabled = false
    mapView.settings.compassButton    = false   // (no direct Android equivalent, kept off for parity)
    mapView.settings.indoorPicker     = false   // mapToolbarEnabled = false (closest equivalent)
    mapView.isMyLocationEnabled       = false   // isMyLocationEnabled = false

    // Create the holder — retains delegates for the view's lifetime
    let holder = GMSMapHolder(
        mapView: mapView,
        onMapClick: onMapClick,
        onCurrentLocation: currentLocation,
        goToCurrentLocation: goToCurrentLocation,
        navigatedToCurrentLoc: navigatedToCurrentLoc
    )

    // Store holder as an associated object so it is released when mapView is released
    objc_setAssociatedObject(mapView, &kMapHolderKey, holder, .OBJC_ASSOCIATION_RETAIN_NONATOMIC)

    return mapView
}

/// Updates an existing GMSMapView with new state from the Kotlin side.
///
/// Called by `googleMapViewUpdate` (set in iOSApp.swift) from the `UIKitView` `update` block
/// on every recomposition — mirrors Android's `LaunchedEffect` side effects.
func updateGoogleMapView(
    view: UIView,
    friends: [MyLatLng],
    goToCurrentLocation: Bool,
    goToFriendsLatLong: MyLatLng?,
    navigatedToCurrentLoc: @escaping () -> Void
) {
    guard let holder = objc_getAssociatedObject(view, &kMapHolderKey) as? GMSMapHolder else { return }
    holder.updateState(
        friends: friends,
        goToCurrentLocation: goToCurrentLocation,
        goToFriendsLatLong: goToFriendsLatLong,
        navigatedToCurrentLoc: navigatedToCurrentLoc
    )
}
