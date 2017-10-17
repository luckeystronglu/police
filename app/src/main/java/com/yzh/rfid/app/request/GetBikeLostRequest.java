// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GetBikeLostRequest.proto

package com.yzh.rfid.app.request;

public final class GetBikeLostRequest {
  private GetBikeLostRequest() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface GetBikeLostRequestMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:GetBikeLostRequestMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional string session = 1;</code>
     */
    String getSession();
    /**
     * <code>optional string session = 1;</code>
     */
    com.google.protobuf.ByteString
        getSessionBytes();

    /**
     * <code>optional int64 id = 2;</code>
     */
    long getId();

    /**
     * <code>optional int64 bikeId = 3;</code>
     */
    long getBikeId();

    /**
     * <code>optional int64 userId = 4;</code>
     */
    long getUserId();

    /**
     * <code>optional int64 lastDownloadTime = 5;</code>
     */
    long getLastDownloadTime();
  }
  /**
   * Protobuf type {@code GetBikeLostRequestMessage}
   */
  public  static final class GetBikeLostRequestMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:GetBikeLostRequestMessage)
      GetBikeLostRequestMessageOrBuilder {
    // Use GetBikeLostRequestMessage.newBuilder() to construct.
    private GetBikeLostRequestMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private GetBikeLostRequestMessage() {
      session_ = "";
      id_ = 0L;
      bikeId_ = 0L;
      userId_ = 0L;
      lastDownloadTime_ = 0L;
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private GetBikeLostRequestMessage(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      this();
      int mutable_bitField0_ = 0;
      try {
        boolean done = false;
        while (!done) {
          int tag = input.readTag();
          switch (tag) {
            case 0:
              done = true;
              break;
            default: {
              if (!input.skipField(tag)) {
                done = true;
              }
              break;
            }
            case 10: {
              String s = input.readStringRequireUtf8();

              session_ = s;
              break;
            }
            case 16: {

              id_ = input.readInt64();
              break;
            }
            case 24: {

              bikeId_ = input.readInt64();
              break;
            }
            case 32: {

              userId_ = input.readInt64();
              break;
            }
            case 40: {

              lastDownloadTime_ = input.readInt64();
              break;
            }
          }
        }
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        throw e.setUnfinishedMessage(this);
      } catch (java.io.IOException e) {
        throw new com.google.protobuf.InvalidProtocolBufferException(
            e).setUnfinishedMessage(this);
      } finally {
        makeExtensionsImmutable();
      }
    }
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.yzh.rfid.app.request.GetBikeLostRequest.internal_static_GetBikeLostRequestMessage_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.yzh.rfid.app.request.GetBikeLostRequest.internal_static_GetBikeLostRequestMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage.class, com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage.Builder.class);
    }

    public static final int SESSION_FIELD_NUMBER = 1;
    private volatile Object session_;
    /**
     * <code>optional string session = 1;</code>
     */
    public String getSession() {
      Object ref = session_;
      if (ref instanceof String) {
        return (String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        String s = bs.toStringUtf8();
        session_ = s;
        return s;
      }
    }
    /**
     * <code>optional string session = 1;</code>
     */
    public com.google.protobuf.ByteString
        getSessionBytes() {
      Object ref = session_;
      if (ref instanceof String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (String) ref);
        session_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    public static final int ID_FIELD_NUMBER = 2;
    private long id_;
    /**
     * <code>optional int64 id = 2;</code>
     */
    public long getId() {
      return id_;
    }

    public static final int BIKEID_FIELD_NUMBER = 3;
    private long bikeId_;
    /**
     * <code>optional int64 bikeId = 3;</code>
     */
    public long getBikeId() {
      return bikeId_;
    }

    public static final int USERID_FIELD_NUMBER = 4;
    private long userId_;
    /**
     * <code>optional int64 userId = 4;</code>
     */
    public long getUserId() {
      return userId_;
    }

    public static final int LASTDOWNLOADTIME_FIELD_NUMBER = 5;
    private long lastDownloadTime_;
    /**
     * <code>optional int64 lastDownloadTime = 5;</code>
     */
    public long getLastDownloadTime() {
      return lastDownloadTime_;
    }

    private byte memoizedIsInitialized = -1;
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (!getSessionBytes().isEmpty()) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 1, session_);
      }
      if (id_ != 0L) {
        output.writeInt64(2, id_);
      }
      if (bikeId_ != 0L) {
        output.writeInt64(3, bikeId_);
      }
      if (userId_ != 0L) {
        output.writeInt64(4, userId_);
      }
      if (lastDownloadTime_ != 0L) {
        output.writeInt64(5, lastDownloadTime_);
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (!getSessionBytes().isEmpty()) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(1, session_);
      }
      if (id_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(2, id_);
      }
      if (bikeId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(3, bikeId_);
      }
      if (userId_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(4, userId_);
      }
      if (lastDownloadTime_ != 0L) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt64Size(5, lastDownloadTime_);
      }
      memoizedSize = size;
      return size;
    }

    private static final long serialVersionUID = 0L;
    @Override
    public boolean equals(final Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage)) {
        return super.equals(obj);
      }
      com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage other = (com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage) obj;

      boolean result = true;
      result = result && getSession()
          .equals(other.getSession());
      result = result && (getId()
          == other.getId());
      result = result && (getBikeId()
          == other.getBikeId());
      result = result && (getUserId()
          == other.getUserId());
      result = result && (getLastDownloadTime()
          == other.getLastDownloadTime());
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      hash = (37 * hash) + SESSION_FIELD_NUMBER;
      hash = (53 * hash) + getSession().hashCode();
      hash = (37 * hash) + ID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getId());
      hash = (37 * hash) + BIKEID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getBikeId());
      hash = (37 * hash) + USERID_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getUserId());
      hash = (37 * hash) + LASTDOWNLOADTIME_FIELD_NUMBER;
      hash = (53 * hash) + com.google.protobuf.Internal.hashLong(
          getLastDownloadTime());
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @Override
    protected Builder newBuilderForType(
        BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * Protobuf type {@code GetBikeLostRequestMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:GetBikeLostRequestMessage)
        com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.yzh.rfid.app.request.GetBikeLostRequest.internal_static_GetBikeLostRequestMessage_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.yzh.rfid.app.request.GetBikeLostRequest.internal_static_GetBikeLostRequestMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage.class, com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage.Builder.class);
      }

      // Construct using com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage.newBuilder()
      private Builder() {
        maybeForceBuilderInitialization();
      }

      private Builder(
          BuilderParent parent) {
        super(parent);
        maybeForceBuilderInitialization();
      }
      private void maybeForceBuilderInitialization() {
        if (com.google.protobuf.GeneratedMessageV3
                .alwaysUseFieldBuilders) {
        }
      }
      public Builder clear() {
        super.clear();
        session_ = "";

        id_ = 0L;

        bikeId_ = 0L;

        userId_ = 0L;

        lastDownloadTime_ = 0L;

        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.yzh.rfid.app.request.GetBikeLostRequest.internal_static_GetBikeLostRequestMessage_descriptor;
      }

      public com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage getDefaultInstanceForType() {
        return com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage.getDefaultInstance();
      }

      public com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage build() {
        com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage buildPartial() {
        com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage result = new com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage(this);
        result.session_ = session_;
        result.id_ = id_;
        result.bikeId_ = bikeId_;
        result.userId_ = userId_;
        result.lastDownloadTime_ = lastDownloadTime_;
        onBuilt();
        return result;
      }

      public Builder clone() {
        return (Builder) super.clone();
      }
      public Builder setField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.setField(field, value);
      }
      public Builder clearField(
          com.google.protobuf.Descriptors.FieldDescriptor field) {
        return (Builder) super.clearField(field);
      }
      public Builder clearOneof(
          com.google.protobuf.Descriptors.OneofDescriptor oneof) {
        return (Builder) super.clearOneof(oneof);
      }
      public Builder setRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          int index, Object value) {
        return (Builder) super.setRepeatedField(field, index, value);
      }
      public Builder addRepeatedField(
          com.google.protobuf.Descriptors.FieldDescriptor field,
          Object value) {
        return (Builder) super.addRepeatedField(field, value);
      }
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage) {
          return mergeFrom((com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage other) {
        if (other == com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage.getDefaultInstance()) return this;
        if (!other.getSession().isEmpty()) {
          session_ = other.session_;
          onChanged();
        }
        if (other.getId() != 0L) {
          setId(other.getId());
        }
        if (other.getBikeId() != 0L) {
          setBikeId(other.getBikeId());
        }
        if (other.getUserId() != 0L) {
          setUserId(other.getUserId());
        }
        if (other.getLastDownloadTime() != 0L) {
          setLastDownloadTime(other.getLastDownloadTime());
        }
        onChanged();
        return this;
      }

      public final boolean isInitialized() {
        return true;
      }

      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private Object session_ = "";
      /**
       * <code>optional string session = 1;</code>
       */
      public String getSession() {
        Object ref = session_;
        if (!(ref instanceof String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          String s = bs.toStringUtf8();
          session_ = s;
          return s;
        } else {
          return (String) ref;
        }
      }
      /**
       * <code>optional string session = 1;</code>
       */
      public com.google.protobuf.ByteString
          getSessionBytes() {
        Object ref = session_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (String) ref);
          session_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <code>optional string session = 1;</code>
       */
      public Builder setSession(
          String value) {
        if (value == null) {
    throw new NullPointerException();
  }
  
        session_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional string session = 1;</code>
       */
      public Builder clearSession() {
        
        session_ = getDefaultInstance().getSession();
        onChanged();
        return this;
      }
      /**
       * <code>optional string session = 1;</code>
       */
      public Builder setSessionBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) {
    throw new NullPointerException();
  }
  checkByteStringIsUtf8(value);
        
        session_ = value;
        onChanged();
        return this;
      }

      private long id_ ;
      /**
       * <code>optional int64 id = 2;</code>
       */
      public long getId() {
        return id_;
      }
      /**
       * <code>optional int64 id = 2;</code>
       */
      public Builder setId(long value) {
        
        id_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 id = 2;</code>
       */
      public Builder clearId() {
        
        id_ = 0L;
        onChanged();
        return this;
      }

      private long bikeId_ ;
      /**
       * <code>optional int64 bikeId = 3;</code>
       */
      public long getBikeId() {
        return bikeId_;
      }
      /**
       * <code>optional int64 bikeId = 3;</code>
       */
      public Builder setBikeId(long value) {
        
        bikeId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 bikeId = 3;</code>
       */
      public Builder clearBikeId() {
        
        bikeId_ = 0L;
        onChanged();
        return this;
      }

      private long userId_ ;
      /**
       * <code>optional int64 userId = 4;</code>
       */
      public long getUserId() {
        return userId_;
      }
      /**
       * <code>optional int64 userId = 4;</code>
       */
      public Builder setUserId(long value) {
        
        userId_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 userId = 4;</code>
       */
      public Builder clearUserId() {
        
        userId_ = 0L;
        onChanged();
        return this;
      }

      private long lastDownloadTime_ ;
      /**
       * <code>optional int64 lastDownloadTime = 5;</code>
       */
      public long getLastDownloadTime() {
        return lastDownloadTime_;
      }
      /**
       * <code>optional int64 lastDownloadTime = 5;</code>
       */
      public Builder setLastDownloadTime(long value) {
        
        lastDownloadTime_ = value;
        onChanged();
        return this;
      }
      /**
       * <code>optional int64 lastDownloadTime = 5;</code>
       */
      public Builder clearLastDownloadTime() {
        
        lastDownloadTime_ = 0L;
        onChanged();
        return this;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:GetBikeLostRequestMessage)
    }

    // @@protoc_insertion_point(class_scope:GetBikeLostRequestMessage)
    private static final com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage();
    }

    public static com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<GetBikeLostRequestMessage>
        PARSER = new com.google.protobuf.AbstractParser<GetBikeLostRequestMessage>() {
      public GetBikeLostRequestMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new GetBikeLostRequestMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<GetBikeLostRequestMessage> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<GetBikeLostRequestMessage> getParserForType() {
      return PARSER;
    }

    public com.yzh.rfid.app.request.GetBikeLostRequest.GetBikeLostRequestMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_GetBikeLostRequestMessage_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_GetBikeLostRequestMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n\030GetBikeLostRequest.proto\"r\n\031GetBikeLos" +
      "tRequestMessage\022\017\n\007session\030\001 \001(\t\022\n\n\002id\030\002" +
      " \001(\003\022\016\n\006bikeId\030\003 \001(\003\022\016\n\006userId\030\004 \001(\003\022\030\n\020" +
      "lastDownloadTime\030\005 \001(\003B2\n\034com.yzh.rfidbi" +
      "ke.app.requestB\022GetBikeLostRequestb\006prot" +
      "o3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_GetBikeLostRequestMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_GetBikeLostRequestMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_GetBikeLostRequestMessage_descriptor,
        new String[] { "Session", "Id", "BikeId", "UserId", "LastDownloadTime", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}