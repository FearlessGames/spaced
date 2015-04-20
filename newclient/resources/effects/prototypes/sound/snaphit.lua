local effect = CreateSoundPrototype("/sound/sfx/events/hits/snaphit.wav")

effect:setFalloff(1.5)
effect:setGain(1.7 * 3)
effect:setLooping(false)
effect:setPitch(1.0)
effect:setReferenceDistance(1.8)

return effect