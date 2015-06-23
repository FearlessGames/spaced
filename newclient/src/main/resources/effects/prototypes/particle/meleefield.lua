local particles = CreateParticlePrototype("meleefield", "/textures/particles/dot.png", 25)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.0002)
particles:setStartSize(0.0)
particles:setEndSize(0.6)
particles:setMinimumLifetime(200)
particles:setMaximumLifetime(1000)
particles:setStartColor(0.9, 0.51, 0.0, 0.1)
particles:setEndColor(0.8, 0.6, 0.0, 0.0)
particles:setMaximumAngle(330)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
