package com.jayice.goblinfashionengineapi.api.legacy.mapper;

import com.jayice.goblinfashionengineapi.api.domain.enums.*;
import com.jayice.goblinfashionengineapi.api.domain.model.Shiny;
import com.jayice.goblinfashionengineapi.api.legacy.model.LegacyShiny;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Maps legacy inventory records to canonical {@link Shiny} records.
 */
@Component
public class ShinyMapper {

    /**
     * Converts one legacy shiny record into canonical shape.
     *
     * @param legacy legacy source record
     * @return canonical shiny, or {@code null} when source is {@code null}
     */
    public Shiny toCanonical(LegacyShiny legacy) {
        if (legacy == null) {
            return null;
        }

        ShinyCategory category = toShinyCategory(legacy.getCategory());

        Shiny.ShinyBuilder builder = Shiny.builder()
                .id(legacy.getId())
                .name(legacy.getName() == null ? legacy.getId() : legacy.getName())
                .count(legacy.getCount() == null ? 0 : legacy.getCount())
                .category(category)
                .layer(toLayer(category))
                .officeOk(Boolean.TRUE.equals(legacy.getOfficeOk()))
                .publicWear(Boolean.TRUE.equals(legacy.getPublicWear()))
                .includeInEngine(Boolean.TRUE.equals(legacy.getIncludeInEngine()));

        mapOptionalFields(legacy, builder);
        return builder.build();
    }

    private void mapOptionalFields(LegacyShiny legacy, Shiny.ShinyBuilder builder) {
        if (legacy.getSubcategory() != null) {
            builder.subcategory(legacy.getSubcategory());
        }
        if (legacy.getFabric() != null) {
            builder.fabric(legacy.getFabric());
        }
        if (legacy.getFit() != null) {
            builder.fit(legacy.getFit());
        }
        if (legacy.getWarmth() != null) {
            builder.warmth(legacy.getWarmth());
        }
        if (legacy.getImagePath() != null) {
            builder.imagePath(legacy.getImagePath());
        }
        if (legacy.getNotes() != null) {
            builder.notes(legacy.getNotes());
        }

        List<Context> contexts = toContexts(legacy.getPrimaryContext(), legacy.getSecondaryContext());
        if (!contexts.isEmpty()) {
            builder.contexts(contexts);
        }

        Formality formality = toFormality(legacy.getFormality());
        if (formality != null) {
            builder.formality(formality);
        }

        Attention attention = toAttention(legacy.getAttentionLevel());
        if (attention != null) {
            builder.attention(attention);
        }

        Color colorPrimary = toColor(legacy.getColorPrimary());
        if (colorPrimary != null) {
            builder.colorPrimary(colorPrimary);
        }

        Color colorSecondary = toColor(legacy.getColorSecondary());
        if (colorSecondary != null) {
            builder.colorSecondary(colorSecondary);
        }

        Pattern pattern = toPattern(legacy.getPattern());
        if (pattern != null) {
            builder.pattern(pattern);
        }

        EngineInclusionPolicy inclusionPolicy = toEngineInclusionPolicy(legacy.getIncludeInEngine());
        if (inclusionPolicy != null) {
            builder.engineInclusionPolicy(inclusionPolicy);
        }

        ShinyStatus status = toStatus(legacy.getStatus());
        if (status != null) {
            builder.status(status);
        }
    }

    /**
     * Converts a legacy list into canonical shape.
     *
     * @param legacyList legacy list
     * @return canonical list, never {@code null}
     */
    public List<Shiny> toCanonicalList(List<LegacyShiny> legacyList) {
        if (legacyList == null || legacyList.isEmpty()) {
            return List.of();
        }

        return legacyList.stream()
                .filter(item -> item != null)
                .map(this::toCanonical)
                .toList();
    }

    private ShinyCategory toShinyCategory(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }

        return switch (normalized) {
            case "ACCESSORIES", "ACESSORIES" -> ShinyCategory.ACCESSORY;
            case "ATHLETIC_GEAR" -> ShinyCategory.ACTIVEWEAR;
            case "PANTS", "SHORTS" -> ShinyCategory.BOTTOM;
            case "SHIRTS" -> ShinyCategory.TOP;
            case "SOCKS", "UNDERWEAR" -> ShinyCategory.UNDERGARMENT;
            default -> valueOfEnum(normalized, ShinyCategory.class);
        };
    }

    private Layer toLayer(ShinyCategory category) {
        if (category == null) {
            return null;
        }

        return switch (category) {
            case ACCESSORY, JEWELLERY, BAG -> Layer.ACCESSORY;
            case OUTERWEAR -> Layer.OUTER;
            case SHOES -> Layer.LEGWEAR;
            case UNDERGARMENT -> Layer.UNDERLAYER;
            case TOP -> Layer.MID;
            default -> Layer.BASE;
        };
    }

    private List<Context> toContexts(String primary, String secondary) {
        List<Context> contexts = new ArrayList<>(2);
        addContextIfPresent(contexts, primary);
        addContextIfPresent(contexts, secondary);
        return contexts;
    }

    private void addContextIfPresent(List<Context> contexts, String value) {
        Context context = toContext(value);
        if (context != null && !contexts.contains(context)) {
            contexts.add(context);
        }
    }

    private Context toContext(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }

        if ("EVENT".equals(normalized)) {
            return Context.FORMAL;
        }

        return valueOfEnum(normalized, Context.class);
    }

    private Formality toFormality(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }

        return switch (normalized) {
            case "ALWAYS", "FUNCTIONAL", "GYM" -> Formality.CASUAL;
            default -> valueOfEnum(normalized, Formality.class);
        };
    }

    private Attention toAttention(String value) {
        return valueOfEnum(normalize(value), Attention.class);
    }

    private Color toColor(String value) {
        return valueOfEnum(normalize(value), Color.class);
    }

    private Pattern toPattern(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }

        return switch (normalized) {
            case "MICROPATTERN" -> Pattern.MICRO_PATTERN;
            case "MICROPLAID" -> Pattern.MICRO_PLAID;
            default -> valueOfEnum(normalized, Pattern.class);
        };
    }

    private EngineInclusionPolicy toEngineInclusionPolicy(Boolean includeInEngine) {
        if (includeInEngine == null) {
            return null;
        }
        return includeInEngine ? EngineInclusionPolicy.NORMAL : EngineInclusionPolicy.EXCLUDE;
    }

    private ShinyStatus toStatus(String value) {
        return valueOfEnum(normalize(value), ShinyStatus.class);
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        String normalized = value.trim().toUpperCase(Locale.ROOT);
        normalized = normalized.replace("&", "AND");
        normalized = normalized.replaceAll("[^A-Z0-9]+", "_");
        normalized = normalized.replaceAll("_+", "_");
        normalized = normalized.replaceAll("(^_)|(_$)", "");
        return normalized;
    }

    private <E extends Enum<E>> E valueOfEnum(String normalized, Class<E> enumClass) {
        if (normalized == null) {
            return null;
        }

        try {
            return Enum.valueOf(enumClass, normalized);
        } catch (IllegalArgumentException exception) {
            return null;
        }
    }
}
