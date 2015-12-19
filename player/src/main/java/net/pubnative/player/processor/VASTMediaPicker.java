package net.pubnative.player.processor;

import net.pubnative.player.model.VASTMediaFile;

import java.util.List;

public interface VASTMediaPicker {

    VASTMediaFile pickVideo(List<VASTMediaFile> list);
}
