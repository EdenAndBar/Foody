import CoreLocation

class LocationManager: NSObject, ObservableObject, CLLocationManagerDelegate {
    private let manager = CLLocationManager()
    @Published var location: CLLocationCoordinate2D?

    override init() {
        super.init()
        manager.delegate = self
        manager.requestWhenInUseAuthorization()
    }

    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        if let loc = locations.first {
            DispatchQueue.main.async {
                self.location = loc.coordinate
                print("📍 Updated location: \(loc.coordinate.latitude), \(loc.coordinate.longitude)")
                self.manager.stopUpdatingLocation()
            }
        }
    }

    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("❌ Location error: \(error.localizedDescription)")
    }

    func refreshLocation() {
        print("🔄 Refreshing location manually")
        manager.startUpdatingLocation()
    }
}
