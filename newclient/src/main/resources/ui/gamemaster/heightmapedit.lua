if IsGm() then

	function GM_EditTerrain(heightDelta, radius, x, y, z)
		local c = -(heightDelta/(radius * radius))

		local function calcHeight(x1, z1)
			return c * (((x - x1)*(x - x1)) + ((z - z1)*(z - z1))) + heightDelta
		end

		local points = GM_GetNearbyHeightmapPoints(radius, x, y, z)
		for p in iter(points) do
			GM_AdjustHeightmapDataPoint(calcHeight(p:GetX(), p:GetZ()), p:GetIndices())
		end
		GM_InvalidateTerrain()
	end

else
	GM_AdjustTerrain = nil
	GM_InvalidateTerrain = nil
	GM_GetNearbyHeightmapPoints = nil
	GM_AdjustHeightmapDataPoint = nil
	GM_WriteHeightToImage = nil
	GM_WriteHeightToRaw = nil
end