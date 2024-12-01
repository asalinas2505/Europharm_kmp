//
//  StateFlowExtensions.swift
//  iosApp
//
//  Created by Oroimena X on 29/11/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import Combine
import ComposeApp

extension Kotlinx_coroutines_coreStateFlow {
    func asPublisher<T>() -> AnyPublisher<T, Never> {
        let passthrough = PassthroughSubject<T, Never>()
        KotlinFlowUtils.collect(flow: self) { value in
            passthrough.send(value as! T)
        }
        return passthrough.eraseToAnyPublisher()
    }
}
