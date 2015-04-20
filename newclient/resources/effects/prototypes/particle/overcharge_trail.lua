local particles = CreateParticlePrototype("overcharge_trail", "/textures/particles/shockwave.png", 25)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0001)
particles:setStartSize(0.2)
particles:setEndSize(0.8)
particles:setMinimumLifetime(300)
particles:setMaximumLifetime(600)
particles:setStartColor(0, 0.0, 1.0, 0.5)
particles:setEndColor(1, 0.6, 0.0, 0.2)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
