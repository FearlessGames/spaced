local particles = CreateParticlePrototype("overcharge-shock", "/textures/particles/shockwave.png", 1)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0001)
particles:setStartSize(6.5)
particles:setEndSize(4.0)
particles:setMinimumLifetime(200)
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
