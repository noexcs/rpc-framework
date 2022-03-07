package org.noexcs.service;

import org.springframework.stereotype.Service;

/**
 * @author com.noexcept
 * @since 1/10/2022 10:57 AM
 */
@Service
public class HelloServiceImpl implements HelloService {
    @Override
    public String hello(String message) {
        return message.toUpperCase();
    }
}
