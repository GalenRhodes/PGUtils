package com.projectgalen.lib.utils;

// ===========================================================================
//     PROJECT: PGUtils
//    FILENAME: DefaultProgressReporter.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: September 26, 2023
//
// Copyright © 2023 Project Galen. All rights reserved.
//
// Permission to use, copy, modify, and distribute this software for any
// purpose with or without fee is hereby granted, provided that the above
// copyright notice and this permission notice appear in all copies.
//
// THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
// WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
// MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR ANY
// SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
// WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
// ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF OR
// IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
// ===========================================================================

public class DefaultProgressReporter implements ProgressReporter {

    private String message      = null;
    private String progressText = null;
    private int    progress     = 0;
    private int    progressMin  = 0;
    private int    progressMax  = 100;

    public DefaultProgressReporter() { }

    public @Override String getMessage() {
        return message;
    }

    public @Override int getProgress() {
        return Math.max(Math.min(progressMax, progressMin), Math.min(Math.max(progressMax, progressMin), progress));
    }

    public @Override int getProgressMax() {
        return Math.max(progressMax, progressMin);
    }

    public @Override int getProgressMin() {
        return Math.min(progressMax, progressMin);
    }

    public @Override String getProgressText() {
        return U.toString(progressText, () -> {
            double lo = getProgressMin();
            return "%d%%".formatted((int)(((((double)getProgress()) - lo) / (((double)getProgressMax()) - lo)) * 100.0));
        });
    }

    public @Override void setMessage(String text) {
        message = text;
    }

    public @Override void setProgress(int value) {
        progress = value;
    }

    public @Override void setProgressMax(int value) {
        progressMax = value;
    }

    public @Override void setProgressMin(int value) {
        progressMin = value;
    }

    public @Override void setProgressText(String text) {
        progressText = text;
    }
}
