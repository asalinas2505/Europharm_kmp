import SwiftUI

struct ContentView: View {
    @StateObject private var scanner = BarcodeScannerWrapper()

    var body: some View {
        HStack {
            VStack {
                Text("Cámara")
                Spacer()
                Text("Vista de la cámara aquí")
            }
            VStack {
                Text("Código escaneado:")
                Text(scanner.scannedText)
                    .font(.headline)
                    .foregroundColor(.blue)
                    .padding()
                Spacer()
                Button("Iniciar Escaneo") {
                    scanner.startScanning()
                }
                Button("Detener Escaneo") {
                    scanner.stopScanning()
                }
            }
        }
        .padding()
    }
}
