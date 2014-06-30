package de.persosim.simulator.tlv;

/**
 * org.globaltester.motemri.basic.tlv is a collection of classes for creating,
 * analyzing, handling and manipulating TLV data objects and sequences of them
 * as well as their basic components, i.e. tag, length and value fields. The
 * provided functionality is geared but not restricted to the needs of PersoSim
 * and may be extended at any time...
 * 
 * All classes and methods within this package internally store TLV elements and
 * objects in BER encoding. As there may exist multiple equally valid encodings
 * of the same content the classes and methods keep original BER-TLV encoding as
 * long as possible. This means that in cases where there may exist multiple
 * equally valid BER compliant encodings the provided encoding will not be
 * modified unless explicitly stated by direct request or logically implied by
 * other operations. In cases when a BER encoded TLV element is changed or set
 * without providing an exact representation, DER encoding is used as default
 * encoding. As DER encoding as a sub group of BER encoding only allows for
 * exactly one valid encoding of the same content the behavior of classes and
 * methods concerning treatment of encodings is always predictable.
 * 
 * Example: Object A has been created as a TLV data object with primitive
 * encoding. Object B has been created as a TLV data object with constructed
 * encoding containing Object A as its only child object in its data field. If
 * the length field of Object A is encoded according to BER encoding rules but
 * does not comply with DER encoding rules, the BER encoding is set as normal.
 * This BER encoded field will be returned as length whenever Object B is asked
 * for its length field and the encoded value matches the actual length of the
 * value field. If, however, the total length of Object A is changed, i.e. by
 * setting larger value field, this also implicitly changes the length of Object
 * B's value field and hence also its length field. The next time B is asked for
 * its length field the method serving this request will notice the mismatch of
 * value and length field and discard the explicitly provided length field of B.
 * Instead it will return a by default DER encoded length field matching the
 * actual current length of the value field. This updating process is performed
 * recursively on all child objects to return a valid field.
 * 
 * All access to the tag field is limited to methods provided by the object
 * which it is part of. Length field and value field of any TLV data object may
 * be set and edited freely from outside this package. Validity of the fields is
 * checked every time one of these fields is accessed. See {@link TlvDataObject}
 * for further details, e.g. possible inconsistencies of length and value
 * fields.
 * 
 * In general objects within this package will check the content of external
 * parameters or internal variables before they are used. This is due to the
 * possibility that these may have been or will be changed by outside access,
 * e.g. an explicitly set length field becomes invalid by adding another TLV
 * data object as a child. These checks are enabled by default but may be
 * intentionally disabled. Wherever this is allowed an additional boolean
 * variable may be added to the respective constructor or set() method arguments. Also
 * an explicit separate method
 * {@link TlvDataObject#setPerformValidityChecksTo(boolean)} is available.
 * Parameters in both cases may either be
 * {@link ValidityChecks#PERFORM_VALIDITY_CHECKS} or
 * {@link ValidityChecks#SKIP_VALIDITY_CHECKS}. When set for an object the
 * parameter will stay active for as long as the object exists or it is not
 * reset with the before mentioned method. Skipping validity checks will result
 * in validity checks no longer being performed. It can only be declared for one
 * object or element at a time. This explicitly means that if validity checks
 * are to be performed recursively, i.e. for constructed TLV data objects with
 * several maybe nested child objects, skipping will be limited to the levels on
 * which it has been declared.
 * 
 * Constructors or methods within this package that expect a byte array
 * accompanied by two offsets have the following requirements unless explicitly
 * stated otherwise:
 * 
 * byte array: the byte array from which a certain range is to be used
 * 
 * minOffset: the first offset to be part of the range (inclusive)
 * 
 * maxOffset: the first offset no longer to be part of the range (exclusive)
 * This offset may lay outside of the specified byte array.
 * 
 * The range itself must contain the whole field to be processed. The first byte
 * of the range must also be the first byte of the field to be processed. The
 * last byte of the range may no longer be part of the field to be processed
 * 
 * This notation is used to simplify the construction of TLV elements from raw
 * byte arrays with unknown offsets and lengths of elements.
 */
