local particles = CreateParticlePrototype("overcharge_charge", "/textures/particles/energybullet.png", 22)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0002)
particles:setStartSize(0.3)
particles:setStartSpin(0.2)
particles:setEndSpin(0.04)
particles:setEndSize(0.0)
particles:setMinimumLifetime(1100)
particles:setMaximumLifetime(2800)
particles:setStartColor(0, 0.0, 0.8, 1)
particles:setEndColor(1, 0.3, 0.0, 0.6)
particles:setMaximumAngle(40)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(15)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

-- type (0 CYLINDER, 1 TORUS), height, radius, strenght, divergence, random (true/false), transformWithScene (true/false)
particles:addVortexInfluence(1, -0.004, -0.002, 0.00041, -0.01, true, true)

return particles
