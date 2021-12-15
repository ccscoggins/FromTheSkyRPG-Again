/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame.ngin;

/**
 * Functional Interface used to define 3-argument No-return lambda functions that enact
 *  side effects on the inputs.
 * @author cameron
 * @param <A> the first parametrized class
 * @param <B> the second parametrized class
 * @param <C> the third parametrized class
 */
@FunctionalInterface
public interface TriConsumer <A, B, C> {
    public void apply(A a, B b, C c);
}
