local particles = CreateParticlePrototype("overcharge_sparks", "/textures/particles/sinespin.png", 11)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.000210)
particles:setStartSize(0.12)
particles:setEndSize(0.0)

particles:setStartSpin(-10.0)
particles:setEndSpin(20.0)

particles:setMinimumLifetime(900)
particles:setMaximumLifetime(1200)
particles:setStartColor(0, 0.0, 1.0, 0)
particles:setEndColor(1, 0.0, 0.0, 1)
particles:setMaximumAngle(360)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

-- type (0 CYLINDER, 1 TORUS), height, radius, strenght, divergence, random (true/false), transformWithScene (true/false)
particles:addVortexInfluence(1, 0.00314, -0.0031, -0.00031, 0.031, false, true)

return particles
