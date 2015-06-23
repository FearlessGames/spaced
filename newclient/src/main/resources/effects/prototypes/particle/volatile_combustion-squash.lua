local particles = CreateParticlePrototype("overcharge-squash", "/textures/particles/dot.png", 6)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0351)
particles:setStartSize(9.1)
particles:setEndSize(0.0)
particles:setMinimumLifetime(80)
particles:setMaximumLifetime(220)
particles:setStartColor(1, 0.0, 0.2, 1)
particles:setEndColor(0, 1.0, 0.0, 0.6)
particles:setMaximumAngle(360)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
