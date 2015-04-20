local particles = CreateParticlePrototype("overcharge_trail", "/textures/particles/dot.png", 25)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0021)
particles:setStartSize(0.8)
particles:setEndSize(1.2)
particles:setMinimumLifetime(300)
particles:setMaximumLifetime(600)
particles:setStartColor(1, 0.0, 0.2, 1)
particles:setEndColor(0, 1.0, 0.0, 0.0)
particles:setMaximumAngle(160)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
