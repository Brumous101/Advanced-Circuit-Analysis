package org.mathpiper.ui.gui.applications.circuitpiper.circuitjs1.stubs;

/*
 * Copyright 2010 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
/**
 * HTML 5 Canvas text metrics.
 *
 * @see <a href="http://www.w3.org/TR/2dcontext/#textmetrics">HTML Canvas 2D
 * TextMetrics</a>
 */
public class TextMetrics extends JavaScriptObject {

    protected TextMetrics() {
    }

    /**
     * Return the width of the rendered text.
     *
     * @return the width of the text
     */
    public final native double getWidth() /*-{
    return this.width;
  }-*/;
}
