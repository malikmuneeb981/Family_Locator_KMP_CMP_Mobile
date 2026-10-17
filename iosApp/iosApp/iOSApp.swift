import SwiftUI
import GoogleMaps
import Shared

@main
struct iOSApp: App {
    init() {
        // Initialize Google Maps SDK. The key is read from Info.plist ("GMSApiKey") and is NOT committed.
        // Without a key the map factory stays unset and the shared UI shows an "API key required" dialog.
        let mapsApiKey = (Bundle.main.object(forInfoDictionaryKey: "GMSApiKey") as? String)?
            .trimmingCharacters(in: .whitespacesAndNewlines) ?? ""
        guard !mapsApiKey.isEmpty else { return }
        GMSServices.provideAPIKey(mapsApiKey)

        // ── Factory: called once when UIKitView first composes ────────────────────────────
        // Mirrors the initial setup inside Android's NativeGoogleMap composable.
        // onMapClick / currentLocation lambdas receive KotlinDouble at the Kotlin↔Swift boundary.
        NativeGoogleMap_iosKt.googleMapViewFactory = { onMapClick, currentLocation, goToCurrentLocation, navigatedToCurrentLoc in
            createGoogleMapView(
                onMapClick: { lat, long in
                    onMapClick(lat, long)          // lat/long are KotlinDouble here
                },
                currentLocation: { lat, long in
                    currentLocation(lat, long)     // lat/long are KotlinDouble here
                },
                goToCurrentLocation: goToCurrentLocation.boolValue,
                navigatedToCurrentLoc: {
                    navigatedToCurrentLoc()
                }
            )
        }

        // ── Update: called on every recomposition from UIKitView's update block ──────────
        // Mirrors Android's LaunchedEffect(myFriendsLatLongs) + LaunchedEffect(goToFriendsLatLong)
        // + LaunchedEffect(currentLocation) combined — pushes new state into the GMSMapHolder.
        NativeGoogleMap_iosKt.googleMapViewUpdate = { view, friends, goToCurrentLocation, goToFriendsLatLong, navigatedToCurrentLoc in
            // friends: Kotlin List<MyLatLng> bridged through ObjC as NSArray → [MyLatLng]
            let friendsList    = friends as? [MyLatLng] ?? []
            // goToFriendsLatLong: Kotlin MyLatLng (nullable-optional in Swift)
            let friendLatLong  = goToFriendsLatLong as? MyLatLng

            updateGoogleMapView(
                view: view,
                friends: friendsList,
                goToCurrentLocation: goToCurrentLocation.boolValue,
                goToFriendsLatLong: friendLatLong,
                navigatedToCurrentLoc: {
                    navigatedToCurrentLoc()
                }
            )
        }
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}