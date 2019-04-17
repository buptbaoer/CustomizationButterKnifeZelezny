package com.avast.android.butterknifezelezny.setting;

/**
 * Created by admin on 2016/12/18.
 */

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;


@State(
        name = "FormatSetting",
        storages = {@Storage(
                file = "$APP_CONFIG$/format.xml"
        )}
)
public class FormatSetting implements PersistentStateComponent<Element> {

    private String setFormat;


    public FormatSetting() {
    }

    public static FormatSetting getInstance() {
        return (FormatSetting) ServiceManager.getService(FormatSetting.class);
    }

    @Override
    @Nullable
    public Element getState() {
        Element element = new Element("FormatSetting");
        element.setAttribute("setFormat", this.getSetFormat());
        return element;
    }

    @Override
    public void loadState(Element state) {
        this.setSetFormat(state.getAttributeValue("setFormat"));
    }

    public String getSetFormat() {
        return this.setFormat == null ? StatementGenerator.defaultSetFormat : this.setFormat;
    }

    public void setSetFormat(String setFormat) {
        this.setFormat = setFormat;
    }



}
