import Combine
import ComposeApp

extension Kotlinx_coroutines_coreStateFlow {
    func asCombinePublisher<T>() -> AnyPublisher<T, Never> where T: AnyObject {
        let passthrough = PassthroughSubject<T, Never>()

        KotlinFlowUtils.collect(flow: self) { value in
            if let newValue = value as? T {
                passthrough.send(newValue)
            }
        }

        return passthrough.eraseToAnyPublisher()
    }
}
