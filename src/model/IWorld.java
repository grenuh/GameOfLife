package model;

public interface IWorld {

    boolean[][] getStates();

    void step();

    void generate();
}
