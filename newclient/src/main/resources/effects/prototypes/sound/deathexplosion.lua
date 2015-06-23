local effect = CreateSoundPrototype("/sound/sfx//events/explosions/lowexplosion.wav")

effect:setFalloff(1.0)
effect:setGain(1.2 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(9.0)

return effect