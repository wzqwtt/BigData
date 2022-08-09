package com.wzq.patterns;

import java.util.concurrent.TimeUnit;

/**
 * 工厂模式
 *
 * @author wzq
 * @create 2022-08-09 18:11
 */
public class FactoryDemo1 {
    public static void main(String[] args) {
        ShapeFactory shapeFactory = new ShapeFactory();

        Shape rectangle = shapeFactory.getShape("Rectangle");
        rectangle.draw();

        Shape square = shapeFactory.getShape("Square");
        square.draw();

        Shape circle = shapeFactory.getShape("Circle");
        circle.draw();
    }
}

class ShapeFactory {
    public Shape getShape(String shapeType) {
        if ("Rectangle".equals(shapeType)) {
            return new Rectangle();
        } else if ("Square".equals(shapeType)) {
            return new Square();
        } else if ("Circle".equals(shapeType)) {
            return new Circle();
        }
        return null;
    }
}

interface Shape {
    void draw();
}

class Rectangle implements Shape {
    @Override
    public void draw() {
        System.out.println("Inside Rectangle::draw() method");
    }
}

class Square implements Shape {
    @Override
    public void draw() {
        System.out.println("Inside Square::draw() method");
    }
}

class Circle implements Shape {
    @Override
    public void draw() {
        System.out.println("Inside Circle::draw() method");
    }
}