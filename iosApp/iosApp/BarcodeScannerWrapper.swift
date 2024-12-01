import SwiftUI
import Combine
import ComposeApp

class BarcodeScannerWrapper: ObservableObject {
    @Published var scannedText: String = "Esperando escaneo..."
    private var cancellables = Set<AnyCancellable>()
    private let scanner = BarcodeScanner()

    init() {
        scanner.scannedText.asPublisher()
            .receive(on: DispatchQueue.main)
            .sink { [weak self] text in
                self?.scannedText = text
            }
            .store(in: &cancellables)
    }

    func startScanning() {
        scanner.startScanning()
    }

    func stopScanning() {
        scanner.stopScanning()
    }
}
