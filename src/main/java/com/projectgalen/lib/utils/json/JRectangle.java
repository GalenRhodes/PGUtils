package com.projectgalen.lib.utils.json;

// ===========================================================================
//     PROJECT: PGBudget
//    FILENAME: _Rectangle.java
//         IDE: IntelliJ IDEA
//      AUTHOR: Galen Rhodes
//        DATE: May 16, 2023
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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "x", "y", "width", "height" })
public class JRectangle {
    public @JsonProperty("x")      int x;
    public @JsonProperty("y")      int y;
    public @JsonProperty("width")  int width;
    public @JsonProperty("height") int height;

    public JRectangle() { }

    public JRectangle(@NotNull Rectangle r) {
        this.x      = r.x;
        this.y      = r.y;
        this.width  = r.width;
        this.height = r.height;
    }

    public @JsonIgnore Rectangle getBounds() { return new Rectangle(x, y, width, height); }

    public int getHeight()                   { return height; }

    public @JsonIgnore Point getLocation()   { return new Point(x, y); }

    public @JsonIgnore Dimension getSize()   { return new Dimension(width, height); }

    public int getWidth()                    { return width; }

    public int getX()                        { return x; }

    public int getY()                        { return y; }

    public void setHeight(int height)        { this.height = height; }

    public void setWidth(int width)          { this.width = width; }

    public void setX(int x)                  { this.x = x; }

    public void setY(int y)                  { this.y = y; }
}
