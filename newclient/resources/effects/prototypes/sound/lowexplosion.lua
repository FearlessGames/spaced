local effect = CreateSoundPrototype("/sound/sfx/events/explosions/lowexplosion.wav")

effect:setFalloff(0.4)
effect:setGain(0.9 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(9.0)

return effect