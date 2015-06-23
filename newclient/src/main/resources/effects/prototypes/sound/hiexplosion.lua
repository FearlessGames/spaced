local effect = CreateSoundPrototype("/sound/sfx//events/explosions/hiexplosion.wav")

effect:setFalloff(1.8)
effect:setGain(1.4 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(1.6)

return effect