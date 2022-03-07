package org.noexcs.service;

import org.springframework.stereotype.Service;

/**
 * @author noexcs
 * @since 3/7/2022 3:37 PM
 */
@Service
public class StringUpperCaseService {

    String upperCaseString(String s) {
        return s.toUpperCase();
    }
}
