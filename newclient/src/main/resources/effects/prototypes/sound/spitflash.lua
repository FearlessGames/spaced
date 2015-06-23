local effect = CreateSoundPrototype("/sound/sfx/events/flashes/spitflash.wav")

effect:setFalloff(1.7)
effect:setGain(0.6 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(1.8)

return effect