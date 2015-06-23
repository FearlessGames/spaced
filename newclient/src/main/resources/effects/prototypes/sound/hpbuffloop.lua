local effect = CreateSoundPrototype("/sound/sfx/buffs/hpbuffloop.wav")

effect:setFalloff(1.0)
effect:setGain(0.04 * 3)
effect:setLooping(true)
effect:setPitch(1.0)
effect:setReferenceDistance(3.0)

return effect