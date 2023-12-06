package com.projectgalen.lib.utils.text.regex;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: MatchIterator.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: December 05, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any purpose with or without fee is hereby granted, provided
// that the above copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
// WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR
// CONSEQUENTIAL DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN ACTION OF CONTRACT,
// NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ================================================================================================================================

import org.intellij.lang.annotations.Language;
import org.intellij.lang.annotations.MagicConstant;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MatchIterator implements Iterator<MatchPoint> {

    private final Matcher      _matcher;
    private final CharSequence _input;
    private       MatchResult  _lastMatchResult = null;
    private       Boolean      _hasNext         = null;
    private       boolean      _done            = false;

    public MatchIterator(Matcher matcher, CharSequence input) {
        this._matcher = matcher;
        this._input   = input;
    }

    public MatchIterator(@NotNull Pattern pattern, @NotNull CharSequence input) {
        this._input   = input;
        this._matcher = pattern.matcher(input);
    }

    public MatchIterator(@NotNull @NonNls @Language("RegExp") String pattern, @NotNull CharSequence input) {
        this(pattern, 0, input);
    }

    public MatchIterator(@NotNull @NonNls @Language("RegExp") String pattern, @MagicConstant(flagsFromClass = Pattern.class) int flags, @NotNull CharSequence input) {
        this._input   = input;
        this._matcher = Regex.getMatcher(pattern, flags, input);
    }

    public @Override boolean hasNext() { synchronized(this) { return ((!_done) && _hasNext()); } }

    public @Override MatchPoint next() {
        synchronized(this) {
            if(_done) throw new NoSuchElementException();

            if(_hasNext()) {
                MatchPoint matchPoint = new MatchPoint(_lastMatchResult, _matcher.toMatchResult(), _input);
                _lastMatchResult = matchPoint.matchResult();
                _hasNext         = null;
                return matchPoint;
            }

            _done = true;
            return new MatchPoint(_lastMatchResult, null, _input);
        }
    }

    private boolean _hasNext() {
        if(_hasNext == null) _hasNext = _matcher.find();
        return _hasNext;
    }
}
