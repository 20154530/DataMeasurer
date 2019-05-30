package com.example.y_t.datameasurer.Models;

import java.util.ArrayList;
import java.util.List;

public class APInfoSet {
    public int Floor;
    public int PointX;
    public int PointY;
    public int Spoint;
    public int Direction;
    public int Amount;
    public List<APinfo> APS;

    public APInfoSet(int floor, int pointx,int pointy, int spoint, int direction, int amount, List<APinfo> APS) {
        Floor = floor;
        PointX = pointx;
        PointY = pointy;
        Spoint = spoint;
        Direction = direction;
        Amount = amount;
        this.APS = new ArrayList<>(APS) ;
    }
}
