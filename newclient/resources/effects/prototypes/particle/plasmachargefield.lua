local particles = CreateParticlePrototype("plasmachargefield", "/textures/particles/fieldball.png", 25)

--particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.001)
particles:setStartSize(0.5)
particles:setEndSize(0.2)
particles:setMinimumLifetime(100)
particles:setMaximumLifetime(200)
particles:setStartColor(0.0, 0.0, 0, 1)
particles:setEndColor(0.2, 0.3, 1.0, 0)
particles:setMaximumAngle(120)
particles:setControlFlow(true)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("WRAP") -- CLAMP/CYCLE/WRAP

return particles
