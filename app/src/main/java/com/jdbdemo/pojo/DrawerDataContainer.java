package com.jdbdemo.pojo;


/**
 * Created by ci-design-01 on 1/4/16.
 */
public class DrawerDataContainer {

    public String drawertitle;
    public int drawerimg, position;

    public DrawerDataContainer(int position, String drawertitle, int drawerimg) {
        this.position = position;
        this.drawertitle = drawertitle;
        this.drawerimg = drawerimg;
    }

    public String getDrawertitle() {
        return drawertitle;
    }

    public int getDrawerimg() {
        return drawerimg;
    }


}
