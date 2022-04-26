package com.example.samplephotopicker.cameraview.adapters;

import java.io.File;

/**
 * Created by OFFICE-N205 on 2018-03-13.
 */

public class ImageExtended {
    public File file;
    public boolean select;

    public ImageExtended(File _file, boolean _select){
        file = _file;
        select = _select;
    }

    public File getFile(){
        return file;
    }

    public void setFile(File _file){
        file = _file;
    }

    public boolean getSelected(){
        return select;
    }

    public void setSelected(boolean _select){
        select = _select;
    }
}
