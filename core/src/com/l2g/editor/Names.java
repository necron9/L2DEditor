package com.l2g.editor;

public abstract class Names {
    public static final String
            fixtureNames[] = {
            "SHELL_",
            "ground",
            "BOMB_",
            "SHOOTER_"};

    public static final float
            materialParameters[][] = {
            // 0 - friction, 1 - restitution, 2 - density
            {0.2f, 0.05f, 5},    // stone
            {0.2f, 0.05f, 700}},   // wood
            shellParameters[][] = {
                    // - linearDamping, 2 - angularDamping
                    {10f, 0f},         // cannonball
                    {15f, 0f}},        // landmine
            shooterParameters[][] = {
                    // 0 - width, 1 - height, 2, angle, 3 - frequency,
                    // 4 - linearAcceleration, 5 - angleAcceleration,
                    // 6 - firePoints
                    {3, 4, 25f, 0.2f, 10f, 20f, 1f},   // simple
                    {3, 4, 60f, 25f, 4f, 14f, 14f, 2f}},     // double
            shapeParameters[][] = {
                    {2},                       // circle
                    {3, 2},                   // rectangle
                    {2},                       // triangle_left
                    {2},                       // triangle_right
                    {5, 5, 0, 8, 10, 20, 5, 9}, //polygon
                    {2}                        // dynamic boxes
            },
            blastParameters[][] = {
                    // 0 - amountRays, 1 - blastDuration, 2 - blastPower, 3 - blastRadius
                    {15, 30, 400, 0.5f},            // explosive
                    {}},
            bombParameters[][] = {
                    // 0 - width, 1 - height
                    {5, 3},
                    {}
            };

    public final static char
            yesDelete = 'T',
            noDelete = 'F';
}