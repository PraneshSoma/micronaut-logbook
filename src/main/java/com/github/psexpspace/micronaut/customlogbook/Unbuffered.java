package com.github.psexpspace.micronaut.customlogbook;



final class Unbuffered implements State {

    @Override
    public Offering with() {
        return new Offering();
    }

}
