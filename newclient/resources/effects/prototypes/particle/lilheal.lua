local particles = CreateParticlePrototype("lilheal", "/textures/particles/heal.png", 10)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0007)
particles:setStartSize(0.1)
particles:setEndSize(0.200)
particles:setMinimumLifetime(1100)
particles:setMaximumLifetime(1800)
particles:setStartColor(0.2, 0.99, 0.2, 0.99)
particles:setEndColor(0.7, 0.99, 0.7, 0.0)
particles:setMaximumAngle(160)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

-- type (0 CYLINDER, 1 TORUS), height, radius, strenght, divergence, random (true/false), transformWithScene (true/false)

return particles