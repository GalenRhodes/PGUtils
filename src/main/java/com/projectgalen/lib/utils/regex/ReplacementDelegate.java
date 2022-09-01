package com.projectgalen.lib.utils.regex;

import java.util.regex.Matcher;

public interface ReplacementDelegate {
    String getReplacement(Matcher matcher);
}
