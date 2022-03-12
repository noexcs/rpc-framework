package org.noexcs.service;

import org.noexcs.spring.boot.autoconfigure.RpcService;

/**
 * @author noexcs
 * @since 3/7/2022 3:42 PM
 */
@RpcService
public interface StringUpperCaseService {

    String upperCaseString(String s);
}
