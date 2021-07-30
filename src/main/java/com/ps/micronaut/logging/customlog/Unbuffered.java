package com.ps.micronaut.logging.customlog;

final class Unbuffered implements State {

    @Override
    public State with() {
        return new Offering();
    }

}
