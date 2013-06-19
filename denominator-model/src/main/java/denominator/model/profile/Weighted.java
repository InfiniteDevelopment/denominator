package denominator.model.profile;

import static com.google.common.base.Preconditions.checkArgument;
import static denominator.model.ResourceRecordSets.tryFindProfile;

import java.beans.ConstructorProperties;
import java.util.Map;

import com.google.common.collect.ForwardingMap;
import com.google.common.collect.ImmutableMap;

import denominator.model.ResourceRecordSet;

/**
 * Record sets with this profile are load balanced, differentiated by an integer
 * {@code weight}.
 * 
 * <h4>Example</h4>
 * 
 * <pre>
 * Weighted profile = Weighted.create(2);
 * </pre>
 * 
 * @since 1.3
 */
public class Weighted extends ForwardingMap<String, Object> {

    /**
     * returns a Weighted view of the {@code profile} or null if no weighted
     * profile found.
     * 
     * @since 1.3.1
     */
    public static Weighted asWeighted(Map<String, Object> profile) {
        if (profile == null)
            return null;
        if (profile instanceof Weighted)
            return Weighted.class.cast(profile);
        return new Weighted(Integer.class.cast(profile.get("weight")));
    }

    /**
     * returns a Weighted view of the {@code rrset} or null if no weighted
     * profile found.
     * 
     * @since 1.3.1
     */
    public static Weighted asWeighted(ResourceRecordSet<?> rrset) {
        return asWeighted(tryFindProfile(rrset, "weighted").orNull());
    }

    /**
     * @param weight
     *            corresponds to {@link #weight()}
     */
    public static Weighted create(int weight) {
        return new Weighted(weight);
    }

    private final String type = "weighted";
    private final int weight;

    @ConstructorProperties({ "weight" })
    private Weighted(int weight) {
        checkArgument(weight >= 0, "weight must be positive");
        this.weight = weight;
        this.delegate = ImmutableMap.<String, Object> builder()//
                .put("type", type)//
                .put("weight", weight).build();
    }

    /**
     * 
     * {@code 0} to always serve the record. Otherwise, provider-specific range
     * of positive numbers which differentiate the load to send to this record
     * set vs another.
     * 
     * <h4>Note</h4>
     * 
     * In some implementation, such as UltraDNS, only even number weights are
     * supported! For highest portability, use even numbers between
     * {@code 0-100}
     * 
     */
    public int weight() {
        return weight;
    }

    // transient to avoid serializing by default, for example in json
    private final transient ImmutableMap<String, Object> delegate;

    @Override
    protected Map<String, Object> delegate() {
        return delegate;
    }
}