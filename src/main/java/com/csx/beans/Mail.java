package com.csx.beans;

import lombok.*;

import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 *
 * @Description: TODO
 * @author: csx
 * @Date: 2018-07-25
 */
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Mail {
    private String subject;

    private String message;

    private Set<String> receivers;
}
