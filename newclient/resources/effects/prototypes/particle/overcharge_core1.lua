local particles = CreateParticlePrototype("overcharge_core1", "/textures/particles/dot.png", 6)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0229)
particles:setStartSize(0.5)
particles:setEndSize(0.2)
particles:setMinimumLifetime(110)
particles:setMaximumLifetime(110)
particles:setStartColor(0, 0.0, 1.0, 1)
particles:setEndColor(1, 0.6, 0.0, 0.5)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
