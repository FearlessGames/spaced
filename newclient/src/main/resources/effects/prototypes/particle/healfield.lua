local particles = CreateParticlePrototype("healfield", "/textures/particles/dot.png", 8)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(-0.0002)
particles:setStartSize(0.0)
particles:setEndSize(0.6)
particles:setMinimumLifetime(1600)
particles:setMaximumLifetime(2000)
particles:setStartColor(0.3, 0.51, 0.2, 0.4)
particles:setEndColor(0.3, 0.6, 0.4, 0.0)
particles:setMaximumAngle(330)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
