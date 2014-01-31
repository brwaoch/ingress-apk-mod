package com.nianticproject.ingress.gameentity.components;

import com.nianticproject.ingress.gameentity.EntityComponent;

public interface Captured extends EntityComponent { // actually implemenets DynamicComponent but it's irrelevant for now

    public long getCapturedTime();
    public String getCapturingPlayerId();
}