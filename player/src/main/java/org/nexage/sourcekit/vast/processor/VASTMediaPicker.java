//
//  MediaPicker.java
//
//  Copyright (c) 2014 Nexage. All rights reserved.
//

package org.nexage.sourcekit.vast.processor;

import org.nexage.sourcekit.vast.model.VASTMediaFile;

import java.util.List;

public interface VASTMediaPicker {

    VASTMediaFile pickVideo(List<VASTMediaFile> list);
}
