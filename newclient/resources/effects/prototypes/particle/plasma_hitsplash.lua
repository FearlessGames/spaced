local particles = CreateParticlePrototype("plasma_hitsplash", "/textures/particles/dot.png", 11)

particles:setEmissionDirection(0, 1, 0)
particles:setInitialVelocity(0.0251)
particles:setStartSize(1.6)
particles:setEndSize(0.2)
particles:setMinimumLifetime(200)
particles:setMaximumLifetime(360)
particles:setStartColor(1.0, 0.4, 0.0, 1)
particles:setEndColor(1.0, 0.3, 0.2, 1)
particles:setMaximumAngle(360)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(5)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType("CLAMP") -- CLAMP/CYCLE/WRAP

return particles
