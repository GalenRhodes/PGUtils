package com.projectgalen.lib.utils.errors;
// ================================================================================================================================
//     PROJECT: PGUtils
//    FILENAME: PGPropertiesException.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: November 28, 2023
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

public class PGPropertiesException extends RuntimeException {
    public PGPropertiesException() {
        super();
    }

    public PGPropertiesException(String message) {
        super(message);
    }

    public PGPropertiesException(String message, Throwable cause) {
        super(message, cause);
    }

    public PGPropertiesException(Throwable cause) {
        super(cause);
    }
}