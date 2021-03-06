// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: GetBikeAndBikeUserResponse.proto

package com.yzh.rfid.app.response;

public final class GetBikeAndBikeUserResponse {
  private GetBikeAndBikeUserResponse() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  public interface GetBikeAndBikeUserResponseMessageOrBuilder extends
      // @@protoc_insertion_point(interface_extends:GetBikeAndBikeUserResponseMessage)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <code>optional .ErrorMessage errorMsg = 1;</code>
     */
    boolean hasErrorMsg();
    /**
     * <code>optional .ErrorMessage errorMsg = 1;</code>
     */
    com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage getErrorMsg();
    /**
     * <code>optional .ErrorMessage errorMsg = 1;</code>
     */
    com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessageOrBuilder getErrorMsgOrBuilder();

    /**
     * <code>optional .BikeMessage bike = 2;</code>
     */
    boolean hasBike();
    /**
     * <code>optional .BikeMessage bike = 2;</code>
     */
    com.yzh.rfid.app.response.Bike.BikeMessage getBike();
    /**
     * <code>optional .BikeMessage bike = 2;</code>
     */
    com.yzh.rfid.app.response.Bike.BikeMessageOrBuilder getBikeOrBuilder();

    /**
     * <code>optional .BikeUserMessage bikeUser = 3;</code>
     */
    boolean hasBikeUser();
    /**
     * <code>optional .BikeUserMessage bikeUser = 3;</code>
     */
    com.yzh.rfid.app.response.BikeUser.BikeUserMessage getBikeUser();
    /**
     * <code>optional .BikeUserMessage bikeUser = 3;</code>
     */
    com.yzh.rfid.app.response.BikeUser.BikeUserMessageOrBuilder getBikeUserOrBuilder();
  }
  /**
   * Protobuf type {@code GetBikeAndBikeUserResponseMessage}
   */
  public  static final class GetBikeAndBikeUserResponseMessage extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:GetBikeAndBikeUserResponseMessage)
      GetBikeAndBikeUserResponseMessageOrBuilder {
    // Use GetBikeAndBikeUserResponseMessage.newBuilder() to construct.
    private GetBikeAndBikeUserResponseMessage(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private GetBikeAndBikeUserResponseMessage() {
    }

    @Override
    public final com.google.protobuf.UnknownFieldSet
    getUnknownFields() {
      return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
    }
    private GetBikeAndBikeUserResponseMessage(
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
              com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.Builder subBuilder = null;
              if (errorMsg_ != null) {
                subBuilder = errorMsg_.toBuilder();
              }
              errorMsg_ = input.readMessage(com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(errorMsg_);
                errorMsg_ = subBuilder.buildPartial();
              }

              break;
            }
            case 18: {
              com.yzh.rfid.app.response.Bike.BikeMessage.Builder subBuilder = null;
              if (bike_ != null) {
                subBuilder = bike_.toBuilder();
              }
              bike_ = input.readMessage(com.yzh.rfid.app.response.Bike.BikeMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(bike_);
                bike_ = subBuilder.buildPartial();
              }

              break;
            }
            case 26: {
              com.yzh.rfid.app.response.BikeUser.BikeUserMessage.Builder subBuilder = null;
              if (bikeUser_ != null) {
                subBuilder = bikeUser_.toBuilder();
              }
              bikeUser_ = input.readMessage(com.yzh.rfid.app.response.BikeUser.BikeUserMessage.parser(), extensionRegistry);
              if (subBuilder != null) {
                subBuilder.mergeFrom(bikeUser_);
                bikeUser_ = subBuilder.buildPartial();
              }

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
      return com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.internal_static_GetBikeAndBikeUserResponseMessage_descriptor;
    }

    protected FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.internal_static_GetBikeAndBikeUserResponseMessage_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage.class, com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage.Builder.class);
    }

    public static final int ERRORMSG_FIELD_NUMBER = 1;
    private com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage errorMsg_;
    /**
     * <code>optional .ErrorMessage errorMsg = 1;</code>
     */
    public boolean hasErrorMsg() {
      return errorMsg_ != null;
    }
    /**
     * <code>optional .ErrorMessage errorMsg = 1;</code>
     */
    public com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage getErrorMsg() {
      return errorMsg_ == null ? com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.getDefaultInstance() : errorMsg_;
    }
    /**
     * <code>optional .ErrorMessage errorMsg = 1;</code>
     */
    public com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessageOrBuilder getErrorMsgOrBuilder() {
      return getErrorMsg();
    }

    public static final int BIKE_FIELD_NUMBER = 2;
    private com.yzh.rfid.app.response.Bike.BikeMessage bike_;
    /**
     * <code>optional .BikeMessage bike = 2;</code>
     */
    public boolean hasBike() {
      return bike_ != null;
    }
    /**
     * <code>optional .BikeMessage bike = 2;</code>
     */
    public com.yzh.rfid.app.response.Bike.BikeMessage getBike() {
      return bike_ == null ? com.yzh.rfid.app.response.Bike.BikeMessage.getDefaultInstance() : bike_;
    }
    /**
     * <code>optional .BikeMessage bike = 2;</code>
     */
    public com.yzh.rfid.app.response.Bike.BikeMessageOrBuilder getBikeOrBuilder() {
      return getBike();
    }

    public static final int BIKEUSER_FIELD_NUMBER = 3;
    private com.yzh.rfid.app.response.BikeUser.BikeUserMessage bikeUser_;
    /**
     * <code>optional .BikeUserMessage bikeUser = 3;</code>
     */
    public boolean hasBikeUser() {
      return bikeUser_ != null;
    }
    /**
     * <code>optional .BikeUserMessage bikeUser = 3;</code>
     */
    public com.yzh.rfid.app.response.BikeUser.BikeUserMessage getBikeUser() {
      return bikeUser_ == null ? com.yzh.rfid.app.response.BikeUser.BikeUserMessage.getDefaultInstance() : bikeUser_;
    }
    /**
     * <code>optional .BikeUserMessage bikeUser = 3;</code>
     */
    public com.yzh.rfid.app.response.BikeUser.BikeUserMessageOrBuilder getBikeUserOrBuilder() {
      return getBikeUser();
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
      if (errorMsg_ != null) {
        output.writeMessage(1, getErrorMsg());
      }
      if (bike_ != null) {
        output.writeMessage(2, getBike());
      }
      if (bikeUser_ != null) {
        output.writeMessage(3, getBikeUser());
      }
    }

    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (errorMsg_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(1, getErrorMsg());
      }
      if (bike_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(2, getBike());
      }
      if (bikeUser_ != null) {
        size += com.google.protobuf.CodedOutputStream
          .computeMessageSize(3, getBikeUser());
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
      if (!(obj instanceof com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage)) {
        return super.equals(obj);
      }
      com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage other = (com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage) obj;

      boolean result = true;
      result = result && (hasErrorMsg() == other.hasErrorMsg());
      if (hasErrorMsg()) {
        result = result && getErrorMsg()
            .equals(other.getErrorMsg());
      }
      result = result && (hasBike() == other.hasBike());
      if (hasBike()) {
        result = result && getBike()
            .equals(other.getBike());
      }
      result = result && (hasBikeUser() == other.hasBikeUser());
      if (hasBikeUser()) {
        result = result && getBikeUser()
            .equals(other.getBikeUser());
      }
      return result;
    }

    @Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptorForType().hashCode();
      if (hasErrorMsg()) {
        hash = (37 * hash) + ERRORMSG_FIELD_NUMBER;
        hash = (53 * hash) + getErrorMsg().hashCode();
      }
      if (hasBike()) {
        hash = (37 * hash) + BIKE_FIELD_NUMBER;
        hash = (53 * hash) + getBike().hashCode();
      }
      if (hasBikeUser()) {
        hash = (37 * hash) + BIKEUSER_FIELD_NUMBER;
        hash = (53 * hash) + getBikeUser().hashCode();
      }
      hash = (29 * hash) + unknownFields.hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parseFrom(
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
    public static Builder newBuilder(com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage prototype) {
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
     * Protobuf type {@code GetBikeAndBikeUserResponseMessage}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:GetBikeAndBikeUserResponseMessage)
        com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessageOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.internal_static_GetBikeAndBikeUserResponseMessage_descriptor;
      }

      protected FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.internal_static_GetBikeAndBikeUserResponseMessage_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage.class, com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage.Builder.class);
      }

      // Construct using com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage.newBuilder()
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
        if (errorMsgBuilder_ == null) {
          errorMsg_ = null;
        } else {
          errorMsg_ = null;
          errorMsgBuilder_ = null;
        }
        if (bikeBuilder_ == null) {
          bike_ = null;
        } else {
          bike_ = null;
          bikeBuilder_ = null;
        }
        if (bikeUserBuilder_ == null) {
          bikeUser_ = null;
        } else {
          bikeUser_ = null;
          bikeUserBuilder_ = null;
        }
        return this;
      }

      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.internal_static_GetBikeAndBikeUserResponseMessage_descriptor;
      }

      public com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage getDefaultInstanceForType() {
        return com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage.getDefaultInstance();
      }

      public com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage build() {
        com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      public com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage buildPartial() {
        com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage result = new com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage(this);
        if (errorMsgBuilder_ == null) {
          result.errorMsg_ = errorMsg_;
        } else {
          result.errorMsg_ = errorMsgBuilder_.build();
        }
        if (bikeBuilder_ == null) {
          result.bike_ = bike_;
        } else {
          result.bike_ = bikeBuilder_.build();
        }
        if (bikeUserBuilder_ == null) {
          result.bikeUser_ = bikeUser_;
        } else {
          result.bikeUser_ = bikeUserBuilder_.build();
        }
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
        if (other instanceof com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage) {
          return mergeFrom((com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage other) {
        if (other == com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage.getDefaultInstance()) return this;
        if (other.hasErrorMsg()) {
          mergeErrorMsg(other.getErrorMsg());
        }
        if (other.hasBike()) {
          mergeBike(other.getBike());
        }
        if (other.hasBikeUser()) {
          mergeBikeUser(other.getBikeUser());
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
        com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage parsedMessage = null;
        try {
          parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          parsedMessage = (com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage) e.getUnfinishedMessage();
          throw e.unwrapIOException();
        } finally {
          if (parsedMessage != null) {
            mergeFrom(parsedMessage);
          }
        }
        return this;
      }

      private com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage errorMsg_ = null;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage, com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.Builder, com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessageOrBuilder> errorMsgBuilder_;
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      public boolean hasErrorMsg() {
        return errorMsgBuilder_ != null || errorMsg_ != null;
      }
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      public com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage getErrorMsg() {
        if (errorMsgBuilder_ == null) {
          return errorMsg_ == null ? com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.getDefaultInstance() : errorMsg_;
        } else {
          return errorMsgBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      public Builder setErrorMsg(com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage value) {
        if (errorMsgBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          errorMsg_ = value;
          onChanged();
        } else {
          errorMsgBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      public Builder setErrorMsg(
          com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.Builder builderForValue) {
        if (errorMsgBuilder_ == null) {
          errorMsg_ = builderForValue.build();
          onChanged();
        } else {
          errorMsgBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      public Builder mergeErrorMsg(com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage value) {
        if (errorMsgBuilder_ == null) {
          if (errorMsg_ != null) {
            errorMsg_ =
              com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.newBuilder(errorMsg_).mergeFrom(value).buildPartial();
          } else {
            errorMsg_ = value;
          }
          onChanged();
        } else {
          errorMsgBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      public Builder clearErrorMsg() {
        if (errorMsgBuilder_ == null) {
          errorMsg_ = null;
          onChanged();
        } else {
          errorMsg_ = null;
          errorMsgBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      public com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.Builder getErrorMsgBuilder() {
        
        onChanged();
        return getErrorMsgFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      public com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessageOrBuilder getErrorMsgOrBuilder() {
        if (errorMsgBuilder_ != null) {
          return errorMsgBuilder_.getMessageOrBuilder();
        } else {
          return errorMsg_ == null ?
              com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.getDefaultInstance() : errorMsg_;
        }
      }
      /**
       * <code>optional .ErrorMessage errorMsg = 1;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage, com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.Builder, com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessageOrBuilder> 
          getErrorMsgFieldBuilder() {
        if (errorMsgBuilder_ == null) {
          errorMsgBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage, com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessage.Builder, com.yzh.rfid.app.response.ErrorMessageResponse.ErrorMessageOrBuilder>(
                  getErrorMsg(),
                  getParentForChildren(),
                  isClean());
          errorMsg_ = null;
        }
        return errorMsgBuilder_;
      }

      private com.yzh.rfid.app.response.Bike.BikeMessage bike_ = null;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.yzh.rfid.app.response.Bike.BikeMessage, com.yzh.rfid.app.response.Bike.BikeMessage.Builder, com.yzh.rfid.app.response.Bike.BikeMessageOrBuilder> bikeBuilder_;
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      public boolean hasBike() {
        return bikeBuilder_ != null || bike_ != null;
      }
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      public com.yzh.rfid.app.response.Bike.BikeMessage getBike() {
        if (bikeBuilder_ == null) {
          return bike_ == null ? com.yzh.rfid.app.response.Bike.BikeMessage.getDefaultInstance() : bike_;
        } else {
          return bikeBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      public Builder setBike(com.yzh.rfid.app.response.Bike.BikeMessage value) {
        if (bikeBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          bike_ = value;
          onChanged();
        } else {
          bikeBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      public Builder setBike(
          com.yzh.rfid.app.response.Bike.BikeMessage.Builder builderForValue) {
        if (bikeBuilder_ == null) {
          bike_ = builderForValue.build();
          onChanged();
        } else {
          bikeBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      public Builder mergeBike(com.yzh.rfid.app.response.Bike.BikeMessage value) {
        if (bikeBuilder_ == null) {
          if (bike_ != null) {
            bike_ =
              com.yzh.rfid.app.response.Bike.BikeMessage.newBuilder(bike_).mergeFrom(value).buildPartial();
          } else {
            bike_ = value;
          }
          onChanged();
        } else {
          bikeBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      public Builder clearBike() {
        if (bikeBuilder_ == null) {
          bike_ = null;
          onChanged();
        } else {
          bike_ = null;
          bikeBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      public com.yzh.rfid.app.response.Bike.BikeMessage.Builder getBikeBuilder() {
        
        onChanged();
        return getBikeFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      public com.yzh.rfid.app.response.Bike.BikeMessageOrBuilder getBikeOrBuilder() {
        if (bikeBuilder_ != null) {
          return bikeBuilder_.getMessageOrBuilder();
        } else {
          return bike_ == null ?
              com.yzh.rfid.app.response.Bike.BikeMessage.getDefaultInstance() : bike_;
        }
      }
      /**
       * <code>optional .BikeMessage bike = 2;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.yzh.rfid.app.response.Bike.BikeMessage, com.yzh.rfid.app.response.Bike.BikeMessage.Builder, com.yzh.rfid.app.response.Bike.BikeMessageOrBuilder> 
          getBikeFieldBuilder() {
        if (bikeBuilder_ == null) {
          bikeBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.yzh.rfid.app.response.Bike.BikeMessage, com.yzh.rfid.app.response.Bike.BikeMessage.Builder, com.yzh.rfid.app.response.Bike.BikeMessageOrBuilder>(
                  getBike(),
                  getParentForChildren(),
                  isClean());
          bike_ = null;
        }
        return bikeBuilder_;
      }

      private com.yzh.rfid.app.response.BikeUser.BikeUserMessage bikeUser_ = null;
      private com.google.protobuf.SingleFieldBuilderV3<
          com.yzh.rfid.app.response.BikeUser.BikeUserMessage, com.yzh.rfid.app.response.BikeUser.BikeUserMessage.Builder, com.yzh.rfid.app.response.BikeUser.BikeUserMessageOrBuilder> bikeUserBuilder_;
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      public boolean hasBikeUser() {
        return bikeUserBuilder_ != null || bikeUser_ != null;
      }
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      public com.yzh.rfid.app.response.BikeUser.BikeUserMessage getBikeUser() {
        if (bikeUserBuilder_ == null) {
          return bikeUser_ == null ? com.yzh.rfid.app.response.BikeUser.BikeUserMessage.getDefaultInstance() : bikeUser_;
        } else {
          return bikeUserBuilder_.getMessage();
        }
      }
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      public Builder setBikeUser(com.yzh.rfid.app.response.BikeUser.BikeUserMessage value) {
        if (bikeUserBuilder_ == null) {
          if (value == null) {
            throw new NullPointerException();
          }
          bikeUser_ = value;
          onChanged();
        } else {
          bikeUserBuilder_.setMessage(value);
        }

        return this;
      }
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      public Builder setBikeUser(
          com.yzh.rfid.app.response.BikeUser.BikeUserMessage.Builder builderForValue) {
        if (bikeUserBuilder_ == null) {
          bikeUser_ = builderForValue.build();
          onChanged();
        } else {
          bikeUserBuilder_.setMessage(builderForValue.build());
        }

        return this;
      }
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      public Builder mergeBikeUser(com.yzh.rfid.app.response.BikeUser.BikeUserMessage value) {
        if (bikeUserBuilder_ == null) {
          if (bikeUser_ != null) {
            bikeUser_ =
              com.yzh.rfid.app.response.BikeUser.BikeUserMessage.newBuilder(bikeUser_).mergeFrom(value).buildPartial();
          } else {
            bikeUser_ = value;
          }
          onChanged();
        } else {
          bikeUserBuilder_.mergeFrom(value);
        }

        return this;
      }
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      public Builder clearBikeUser() {
        if (bikeUserBuilder_ == null) {
          bikeUser_ = null;
          onChanged();
        } else {
          bikeUser_ = null;
          bikeUserBuilder_ = null;
        }

        return this;
      }
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      public com.yzh.rfid.app.response.BikeUser.BikeUserMessage.Builder getBikeUserBuilder() {
        
        onChanged();
        return getBikeUserFieldBuilder().getBuilder();
      }
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      public com.yzh.rfid.app.response.BikeUser.BikeUserMessageOrBuilder getBikeUserOrBuilder() {
        if (bikeUserBuilder_ != null) {
          return bikeUserBuilder_.getMessageOrBuilder();
        } else {
          return bikeUser_ == null ?
              com.yzh.rfid.app.response.BikeUser.BikeUserMessage.getDefaultInstance() : bikeUser_;
        }
      }
      /**
       * <code>optional .BikeUserMessage bikeUser = 3;</code>
       */
      private com.google.protobuf.SingleFieldBuilderV3<
          com.yzh.rfid.app.response.BikeUser.BikeUserMessage, com.yzh.rfid.app.response.BikeUser.BikeUserMessage.Builder, com.yzh.rfid.app.response.BikeUser.BikeUserMessageOrBuilder> 
          getBikeUserFieldBuilder() {
        if (bikeUserBuilder_ == null) {
          bikeUserBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
              com.yzh.rfid.app.response.BikeUser.BikeUserMessage, com.yzh.rfid.app.response.BikeUser.BikeUserMessage.Builder, com.yzh.rfid.app.response.BikeUser.BikeUserMessageOrBuilder>(
                  getBikeUser(),
                  getParentForChildren(),
                  isClean());
          bikeUser_ = null;
        }
        return bikeUserBuilder_;
      }
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }

      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return this;
      }


      // @@protoc_insertion_point(builder_scope:GetBikeAndBikeUserResponseMessage)
    }

    // @@protoc_insertion_point(class_scope:GetBikeAndBikeUserResponseMessage)
    private static final com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage();
    }

    public static com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<GetBikeAndBikeUserResponseMessage>
        PARSER = new com.google.protobuf.AbstractParser<GetBikeAndBikeUserResponseMessage>() {
      public GetBikeAndBikeUserResponseMessage parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
          return new GetBikeAndBikeUserResponseMessage(input, extensionRegistry);
      }
    };

    public static com.google.protobuf.Parser<GetBikeAndBikeUserResponseMessage> parser() {
      return PARSER;
    }

    @Override
    public com.google.protobuf.Parser<GetBikeAndBikeUserResponseMessage> getParserForType() {
      return PARSER;
    }

    public com.yzh.rfid.app.response.GetBikeAndBikeUserResponse.GetBikeAndBikeUserResponseMessage getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_GetBikeAndBikeUserResponseMessage_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_GetBikeAndBikeUserResponseMessage_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    String[] descriptorData = {
      "\n GetBikeAndBikeUserResponse.proto\032\032Erro" +
      "rMessageResponse.proto\032\nBike.proto\032\016Bike" +
      "User.proto\"\204\001\n!GetBikeAndBikeUserRespons" +
      "eMessage\022\037\n\010errorMsg\030\001 \001(\0132\r.ErrorMessag" +
      "e\022\032\n\004bike\030\002 \001(\0132\014.BikeMessage\022\"\n\010bikeUse" +
      "r\030\003 \001(\0132\020.BikeUserMessageB;\n\035com.yzh.rfi" +
      "dbike.app.responseB\032GetBikeAndBikeUserRe" +
      "sponseb\006proto3"
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
          com.yzh.rfid.app.response.ErrorMessageResponse.getDescriptor(),
          com.yzh.rfid.app.response.Bike.getDescriptor(),
          com.yzh.rfid.app.response.BikeUser.getDescriptor(),
        }, assigner);
    internal_static_GetBikeAndBikeUserResponseMessage_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_GetBikeAndBikeUserResponseMessage_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_GetBikeAndBikeUserResponseMessage_descriptor,
        new String[] { "ErrorMsg", "Bike", "BikeUser", });
    com.yzh.rfid.app.response.ErrorMessageResponse.getDescriptor();
    com.yzh.rfid.app.response.Bike.getDescriptor();
    com.yzh.rfid.app.response.BikeUser.getDescriptor();
  }

  // @@protoc_insertion_point(outer_class_scope)
}
