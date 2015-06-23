local particles = CreateParticlePrototype("plasma_hit", "/textures/particles/shockwave.png", 4 )

particles:setEmissionDirection(0, 0, 1)
particles:setInitialVelocity(0.0091)
particles:setStartSize(6.0)
particles:setEndSize(0.0)
particles:setMinimumLifetime(200)
particles:setMaximumLifetime(300)
particles:setStartColor(0.3, 0.4, 0.8, 0.31)
particles:setEndColor(0.1, 0.1, 1, 0)
particles:setMaximumAngle(90)
particles:setControlFlow(false)
particles:setParticlesInWorldCoords(true)
particles:warmUp(50)
particles:setZBufferState(true, false)
particles:setBlendState( true , "SourceAlpha" , "One" )
particles:setRepeatType( "CLAMP" ) -- CLAMP/CYCLE/WRAP

return particles
