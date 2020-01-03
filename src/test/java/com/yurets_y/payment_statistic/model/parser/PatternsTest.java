package com.yurets_y.payment_statistic.model.parser;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
public class PatternsTest {
    @Test
    public void matcherGroupTest(){
        String testString = "Сальдо на початок розрахункової доби : в  -4287648.38";
        String stringPattern = "Сальдо на початок.+:.+?(\\d+[,.]\\d+)";
        Pattern pattern = Pattern.compile(stringPattern);
        Matcher matcher = pattern.matcher(testString);
        if(matcher.matches()){
            System.out.println(matcher.group(1));
//            System.out.println(matcher.group(2));
//            System.out.println(matcher.group(2).replaceAll("[,.]",""));
        }

    }
    @Test
    public void simplePatternTest(){
        String testString1 = "Вiдправлення";
        String testString2 = "Вiдправлення - мiжнародне сполучення";
        Pattern pattern = Pattern.compile("Вiдправлення.*");
        assertThat(pattern.matcher(testString1).matches()).isTrue();
        assertThat(pattern.matcher(testString2).matches()).isTrue();
    }
}
