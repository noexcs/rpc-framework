package org.noexcs;

import org.noexcs.service.StringUpperCaseService;

/**
 * @author noexcs
 * @since 3/7/2022 3:44 PM
 */
public class ConsumerMain {
    public static void main(String[] args) {
        StringUpperCaseService upperCaseService = new RpcClientProxy().getProxy(StringUpperCaseService.class);
        String s = upperCaseService.upperCaseString("Hello, World");
        System.out.println(s);
    }
}
